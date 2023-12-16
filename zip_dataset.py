import os
import zipfile
import pathlib
from com.example.epshape import MainActivity

def zip_dataset():
    dataset_dir = MainActivity.DATASET_DIR
    dir = os.environ['HOME']+"/"
    path = pathlib.Path(dir+dataset_dir+"/")
    with zipfile.ZipFile(dir+dataset_dir+".zip", mode="w") as archive:
        for file_path in path.rglob("*"):
                archive.write(
                    file_path,
                arcname=file_path.relative_to(path)
            )