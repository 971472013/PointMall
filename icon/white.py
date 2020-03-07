from PIL import Image
from os import listdir

def alphabg2white_PIL(img):
	img = img.convert('RGBA')
	sp = img.size
	width = sp[0]
	height = sp[1]
	print(sp)
	for yh in range(height):
		for xw in range(width):
			dot = (xw , yh)
			color_d = img.getpixel(dot)
			if (color_d[3] == 0):
				color_d = (255 , 255 , 255 , 255)
				img.putpixel(dot , color_d)
	# img.show()
	return img


if __name__ == '__main__':
	for i in listdir("before"):
		print(i)
		whiteback = alphabg2white_PIL(Image.open("before/"+i))
		whiteback.save("after/"+i , quality = 95)
