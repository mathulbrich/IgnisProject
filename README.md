# IgnisProject

This system propose is soft simulate the Yu-Gi-Oh! VRAINS Ignis' behaviors with the same phrases used in the anime. 

At the moment only AI Ignis will be avaliable, but I expect to create the others Ignis in the future.

This project is in test phase.

## Issues

The project was not tested yet. This because of some problems with my computer, so it may be not working.

## Proxy Screenshots
![AI-STAND](/Screenshots/Ignis_stand_screenshot.png)
![AI-LAUGH](/Screenshots/Ignis_laugh_screenshot.png)

## How to contribute?
```
git clone https://github.com/MathUlbrich/IgnisProject.git
```

## Recognized commands
<ul>
<li>Pray</li>
<li>My god</li>
<li>Who are you</li>
<li>Ai</li>
</ul>

## Subtitles languages
<ul>
<li>English</li>
<li>Portuguese</li>
</ul>

## Technical informations

### Class diagram
![DIAGRAM](/Screenshots/Class_diagram.png)

### Description
**MainActivity**: Is the class on the program will be running. It realize functions like listen the users speechs. Also contain the responses of Ignis to be matched with the user response.

**Response**: Class thats have the structure for Ignis responses.

**AI**: This class is a implementation of the Response class. This class give the concrete voices, messages and images of the response.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
