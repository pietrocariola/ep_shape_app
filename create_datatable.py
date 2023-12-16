import csv
import os
import pathlib
from PIL import Image
from com.example.epshape import MainActivity

def create_datatable():

    path_dataset = pathlib.Path(os.environ["HOME"]+"/"
                                +MainActivity.DATASET_DIR+"/"+MainActivity.INTER_DIR+"/")
    path_csv = os.environ["HOME"]+"/"+MainActivity.DATASET_DIR+"/"+MainActivity.DATA_TABLE_FILE


    list_classes = []
    list_objs = []
    list_bgs = []
    list_pics = []
    for path in path_dataset.rglob("*.jpg"):
        list_classes.append(path.name.split("_")[0])
        list_objs.append(path.name.split("_")[1])
        list_bgs.append(path.name.split("_")[2])
        list_pics.append(path.name.split("_")[3])

    list_classes = list(set(list_classes))
    n_classes = len(list_classes)

    list_objs = list(set(list_objs))
    n_objs = len(list_objs)

    list_bgs = list(set(list_bgs))
    n_bgs = len(list_bgs)

    n_pics = len(list_pics)

    size = 0
    for path in path_dataset.rglob("*.jpg"):
        size += os.path.getsize(path)

    dataset_size = size/1024/1024

    for path in path_dataset.rglob("*.jpg"):
        with Image.open(path) as image:
            img_res = image.size
            break

    imgs_per_class = 0
    for path in path_dataset.rglob(str(list_classes[0])+"/*"):
        imgs_per_class += 1

    with open(path_csv, 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(["Description", "Value"])
        writer.writerow(["number of classes", n_classes])
        writer.writerow(["number of images", n_pics])
        writer.writerow(["dataset size", f"{dataset_size:.2f} MB"])
        writer.writerow(["image resolution", str(img_res[0])+"x"+str(img_res[1])])
        for c in list_classes:
            writer.writerow([c+" images", imgs_per_class])
            writer.writerow([c+" backgrounds", n_bgs])
            writer.writerow([c+" objects", n_objs])