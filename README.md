framework as test task for ryanair job application

How to use:
Maven goal test to run.
Selenium chrome driver must be in path. Selenium chromedriver can be downloaded here: http://chromedriver.storage.googleapis.com/index.html?path=2.24/
Chromedriver can be installed on Mac OS X different ways - I don't have Mac OS X, so I can't check. Instructions from google - http://www.kenst.com/2015/03/installing-chromedriver-on-mac-osx/
If it is imposible to properly install chromedriver - you should put chromedriver file in root of project, or chage configuration file - SeleniumFW\src\main\resources\configs\core.config.xml contains WEBDRIVER_CHROME_DRIVER filed, wich contains expected path to chrome driver. You can change it.

Contains 3 tests, one of them will always fail - to show error handling and screenshot creation


Bright points:
1) It uses pageobjects. Parent pageobject - RyanAirPage. Each unique page has it own pageobject, with specific methods.
Parent function has wide range of methods, that could be used anywere and they are quick way to write test.
Child objects contain more complex and specific methods
2) Screenshot maker. TestListener class makes screenshots of failed tests, screnshot are stored in Cucumber\target\screenshots folder.
3) StaleElementReferenceException handling. Each page object is wraped in proxy class - PageReloaderProxy. And if we get StaleElementReferenceException we reload page object, so we can forget about it.
StaleElementReferenceException happens when selenium works with anchored element, and when this element is gone - when JS chages element for example.
4) Data objects. Good way to make right BDD is to make real-like objects. For example - Person class. Person class contains method to randomly generate data for personal information and to fill in that information on page. So. if we will need to chage functional, all methods with client are stored in one location.
Person class instance with that information then stored, so can be then used again in further tests and beck-end testing.
5) Parallel - all driver classes can run in parallel, TestNG settings must be made for that.
6) Cucumber features stored in Cucumber\src\test\resources\features folder:
Example.feature - is like in task pdf. It has old date, so it is invalid. I use it to show how tests fails and makes screenshot
PR01.feature - reworked example feature with correct date.
PR02.feature - reworked scenario to be more specific. In my opinion, BDD-style cases should look more like this. More specific lines can give feature writer more flexibility, an less lines of code will give more features.
7) Log4j - all logs made with log4j. Log streams into 3 plases - stdout with INFO log, INFO log file, DEBUG log file.

Limitations/TODO list
I had limited time. I tried to show what I can, but I can do more. Here is list I could make easily and what I've deliberately shortcuted
1) TestNG. My current project is not uses cucumber - I implemented cucumber prototype, but team has't decided to switch yet.
Problem with cucumber is it doesn't fits with long end-to-end tests. My current project is BPM system, which have long user stories with more then 10 different actors. Dependencies can work with that well, cucumber features will be too complex.
So I have added possibility to add cucumber features in additional to TestNG scenario, and to work in parallel with it.
 I'm sure user portal like Ryanair is definetely more suitable for cucumber feature testing, but I'll need some insight on proper infrastructure organisation. So I decided to give you example of my work, and not my expectations of how it should be.
2) Russian comments. Most of code I've made myself, some was rewritten by me from different sources. Large portion was written some time ago, when I worked with russian-speaking team.
So some comments are in russian. I've translated some of comments, but not all of them. If this is a problem, I could translate them - but it will take moderate amount of time.
3) Some //TODOs - I've writted comments with //TODO mark everywhere I could do more. Those moments need some investigation and specification. So I implemented quick go-thru
4) Chrome. I have experience in making autotest framework for multiple browsers. But my current project is chrome-only by design (system is designed to work in chrome only).
 I could have demonstrate my ability to work with different brousers, but I can't be sure it will work on Mac OS, and I have no Mac OS near by. So I desided to stick with what I am sure about and not try to cover them all.
5) Waiting to load. Most important thing in good autotest framework - good method to wait for page to load. I could have made funtions, wich finds load image, and them waits for load image to gone. But I didn't catch that image while it loaded.
