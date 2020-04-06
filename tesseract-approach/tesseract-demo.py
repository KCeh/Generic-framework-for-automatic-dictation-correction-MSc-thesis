from PIL import Image
import pytesseract
import cv2
import os

path="./test-pictures/scan0005.png"

#img=Image.open("./test-pictures/edited0001.png")
#img=Image.open("./test-pictures/scan0006.jpg")
#text=pytesseract.image_to_string(img)
#print(text)
#print(pytesseract.image_to_data(Image.open("./test-pictures/edited0001.png")))
#print(pytesseract.image_to_boxes(Image.open(path)))


img = cv2.imread(path)


h, w, c = img.shape
boxes = pytesseract.image_to_boxes(img) 
for b in boxes.splitlines():
    b = b.split(' ')
    img = cv2.rectangle(img, (int(b[1]), h - int(b[2])), (int(b[3]), h - int(b[4])), (0, 255, 0), 2)

filename =os.path.splitext(os.path.basename(path))[0]
path_to_save="./tesseract-results/"+filename+"-result.png"
cv2.imwrite(path_to_save, img)

''' output for scanned document:
By Katherine Glover (2009)
e - Look at the headline and try to interpret it. Then read the story to check your ideas.

1. A friend of mine, on the road for business, recently found himself at a small coffee
shop in South Dakota, where he was served a tepid cup of vile-smelling brown water. My friend
smiled at the waitress, stepped outside, and promptly emptied the cup into the bushes.

2. Before Starbucks was around, he told me, he never would have done such a thing. He
was used to drinking crap coffee. But Starbucks has raised the bar. The ubiquity of Starbucks has
made us expect at least halfway decent coffee no matter where we are - and in response, more
and more players have jumped into the market. As someone recently commented, "these days,
you can get a good cup of coffee at a Chevron station."

3. This leaves Starbucks in quite a bind. Not only is Starbucks perceived as a luxury brand
in a time when people can't afford luxuries, but the "luxury" it offers is readily available
elsewhere.

4. And for less money. Maybe. Though according to a recent Chicago survey, some drinks
at Starbucks are actually cheaper than equivalent drinks at Dunkin' Donuts. Part of Starbucks'
new strategy, in fact, is to debunk the "myth of the $4 latte," as Starbucks marketing VP
Michelle Gass described it. "We have got to correct the misperceptions that are out there," she
said.

5. Then again, it's not exactly difficult to run up a $4 tab at Starbucks, depending on
what you order, and the same Chicago survey confirmed that McDonald's drinks were still
consistently cheaper than those of Starbucks. McDonald's, of course has been aggressively
perpetuating that $4-latte "myth" with its ad campaign for McCafes, centered around the motto
"Four bucks is dumb."

6. To counter this, Starbucks is training its staff to emphasize that most drinks are less
than $3, and it's also offering discounted breakfast pairings. But some analysts think cutting
prices now will "sully its high-end image." Or, as another put it: It is now an admission that
everyone who has said Starbucks was too expensive was right.

7. I'm curious to see what will happen with Starbucks' latest innovation - its upscale
instant coffee, Via. It's not as good as fresh-brewed, apparently, but it's better than most instant
brands. On the other hand, it costs ten times more. Some tasters concluded they'd stick with the
cheap crystals for now. But who knows - maybe in a few years, they'll up their standards and

start dumping that stuff in the bushes.



Handout 7, Poslovni engleski jezik 2 5
'''