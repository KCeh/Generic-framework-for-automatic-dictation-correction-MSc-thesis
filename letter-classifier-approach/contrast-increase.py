from PIL import Image, ImageEnhance
import os

path = os.path.dirname(os.path.abspath(__file__))
dir_for_test_samples=dir_for_results=os.path.join(path, "test-pictures")
dir_for_results=os.path.join(path, "contrast-increase-results")

for (dirpath, dirnames, filenames) in os.walk(dir_for_test_samples):
        for filename in filenames:
            image_path=os.path.join(dir_for_test_samples, filename)
            im = Image.open(image_path).convert("L")
            enhancer = ImageEnhance.Contrast(im)

            splited_filename=os.path.splitext(filename)
            path_to_save=os.path.join(dir_for_results, splited_filename[0]+'-result.png')

            factor = 1.8
            im_output = enhancer.enhance(factor)
            im_output.save(path_to_save)
