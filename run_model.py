from PIL import Image
import torch
import torch.nn as nn
from torch.utils.data import DataLoader, Dataset
from torchvision import transforms
import os

NEW_SIZE = 128
N_CLASSES = 5
CLASS_NAMES = {
    0: 'tesoura',
    1: 'faca',
    2: 'linha',
    3: 'creme',
    4: 'relogio'
}

class MyDataset(Dataset):
    def __init__(self, img_name):
        super().__init__()
        self.img_name = img_name

    def __getitem__(self, i):
        img = Image.open(os.environ['HOME'] + '/' + self.img_name).convert('L')
        transform = transforms.Compose([
            transforms.ToTensor(),
            transforms.Resize(NEW_SIZE)
        ])
        x = transform(img)
        return x

    def __len__(self):
        return 1

class MyNet(nn.Module):
    def __init__(self):
        super().__init__()
        self.cnn = nn.Sequential(
            nn.Conv2d(1, 5, 3, 1, 1),
            nn.ReLU(),
            nn.MaxPool2d(2, 2),
            nn.Conv2d(5, 10, 3, 1, 1),
            nn.ReLU(),
            nn.MaxPool2d(2, 2)
        )
        self.fc = nn.Sequential(
            nn.Linear(NEW_SIZE ** 2 * 10 // 4 // 4, 500),
            nn.Linear(500, N_CLASSES),
            nn.LogSoftmax(dim=1)
        )

    def forward(self, x):
        x = self.cnn(x)
        x = torch.flatten(x, start_dim=1)
        x = self.fc(x)
        return x

def run_model(img_name):
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    model = MyNet()
    model.to(device)
    model.load_state_dict(torch.load(os.environ['HOME']+'/ep_shape_model.pth', map_location=device))
    model.eval()
    dataset = MyDataset(img_name)
    dataloader = DataLoader(dataset, 1)
    for x in dataloader:
        x = x.to(device)
        pred = model(x)
        x_hat = pred.argmax().cpu().detach().numpy()
    return CLASS_NAMES[int(x_hat)]
