# Generic framework for automatic dictation correction
**Program developed for MSc thesis**

Custom OCR engine based on letter classification was developed as part of thesis. Engine is available as web service.  
You can check [Tensorflow 2.0 model based on EMNIST dataset](https://github.com/KCeh/Generic-framework-for-automatic-dictation-correction-MSc-thesis/blob/master/letter-classifier-approach/EMNIST.ipynb)

Web app built with Spring Boot represents generic framework for automatic dictation correction.  
App **can support different OCR methods** (therefore generic framework). **Design pattern strategy** is used to offer support for new OCR methods. One of the supported methods is **Google Vision.** 


Purpose of app is to automate dictate corrections. User can upload photo of written dictation and app will **generate correction based on difference between detected text from photo and text from template.**


**Features of web app:**
* registration, login, logout
* confirm new account by link in mail, reset password by link in mail
* 3 type of user (including admin for user managment)
* create template for dictate (name, language, text, optional audio that can be recored via microphone or uploaded)
* speech to text conversion with Google's Speech-to-Text API (from optinal audio to text for template)
* create dictate correction (user selects template, uploads photo(s) and app generates correction)
* correction shows HTML formated difference between detected text from photo and text from template
* app generates image with highlighted mistakes in correction (wrong words have red bound box, wrong characters are red and characters that should be there are green)

**Example of generated image that contains suggested corrections (written in Croatian language):** 
![Generated image](https://raw.githubusercontent.com/KCeh/Generic-framework-for-automatic-dictation-correction-MSc-thesis/master/images/01.jpg)


Web app is currently hosted on Google Cloud and uses some other Google's services (Cloud Storage, Cloud SQL etc.)

If you have any questions feel free to contact me.