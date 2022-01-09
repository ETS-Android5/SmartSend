<div id="top"></div>
<!--
*** Thanks for checking out the SmartSend. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Ferezhasson%2FSmartSend&count_bg=%23FFB100&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/erezhasson/SmartSend">
    <img src="https://user-images.githubusercontent.com/69516798/148689292-6278d9b8-b000-4266-b842-5c62baedecc3.png" alt="SmartSend Logo">
  </a>

  <h3 align="center">Ritzpa Stock Exchange</h3>

  <p align="center">
    Web Application For Stock Exchanging :dollar: :chart_with_upwards_trend:
    <br />
    <a href="https://github.com/erezhasson/SmartSend"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://youtu.be/4ylUOvWFy60">View Demo</a>
    ·
    <a href="https://github.com/erezhasson/SmartSend/issues">Report Bug</a>
    ·
    <a href="https://github.com/erezhasson/SmartSend/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
<!--         <li><a href="#installation">Installation</a></li> -->
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

![SmartSend Video][product-video]

### General Background
Rizpa Stock Exchange (shortly SmartSend) name is a reference to old stock market's stock exchanging method where traders used to stand in room (called "Trading Floor" - Rizpa in Hebrew translated to floor) and announce which stock is being traded and who is selling it. In the modern age where technology involves at a frequent rate, the stock markets transferred  it's exchanging systems into electronic ones, and this academic project tries to partially clone some of them.

### Project's Structure
SmartSend developed in 3 parts:

1.  Developed a console application where users could "talk" with our system by series of menus and available operations.
2.  Developed a desktop application (via JavaFX architecture) which included a graphic UI on JavaFX components. Each interaction with user were based on different buttons and controllers.
3.  Developed a web application (**this part is mainly covered in this readme, other parts won't be supported here**) which allowed users to dynamically register to system and suggested various operations - issue new stock/buy & sell stocks/deposit cash to system and more.

### On a Personal Note
This project was one of the first time I experienced working in client/server structure, developing a web application and implementing HTTP requests & responses. 
The project summarized  all the subjects that were taught about java & web development. Working on a project from 0 was challenging yet very awarding especially when parts started to connect and form a complete system.

### Built With

* [Java](https://www.java.com/en/)
* [JavaFX](https://openjfx.io/)
* [Apache Tomcat](https://tomcat.apache.org/)
* [Bootstrap](https://getbootstrap.com)
* [JQuery](https://jquery.com)
* HTML
* CSS
* JavaScript
* XML & JSON

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

Nothing but a stable internet connection and understanding <a href="#protocol">systems operation protocol</a></li> :)
* URL
  ```sh
  https://example.com
  ```
  
<div id="protocol"></div>   
  
### System Operations Protocol
In order to issue a new command in system, command structure must be in the following order:  
<p align="center"><b>[TYPE] [SYMBOL] [AMOUNT] [SELLING/BUYING] [PRICE]</b></p><br/>



**TYPE:**  
  
SmartSend system supports the following command types (case sensitive) -
* LMT - Defines limit to stock's price (to/from price) in the command.
 
  > LMT GIT 100 BUYING/SELLING 100 - will buy/sell 100 stocks of GIT, each in price up to/from 100$.

* MKT - Operate this command ASAP (buy/sell in the available  price).
 
  > MKT GIT 100 BUYING/SELLING - will buy/sell 100 stocks of GIT, each in available price in market.

* FOK - Similar to LMT, but the operation must be fully operated or else will be discarded.
 
  > FOK GIT 100 BUYING/SELLING 100 - will buy/sell 100 stocks of GIT, each in price up to/from 100$.  
  > If not all stocks bought/sold, the operation will be discarded.

* IOC - Similar to LMT, but if operation not fully operated it's remaining are discarded.
  > FOK GIT 100 BUYING/SELLING 100 - will buy/sell 100 stocks of GIT, each in price up to/from 100$.  
  > If not all stocks bought/sold, the remaining of the operation will be discarded.
 
 **SYMBOL:** The symbol of the stock (case sensitive).  
 **AMOUNT:** How many stocks in total to sell/buy.  
 **SELLING/BUYING:** Indicates if the command is either selling or buying (case sensitive).  
 **PRICE:** Applies only to LMT type commands. Indicates the price of each stock in command.  
 
 
<!-- ### Installation

_Below is an example of how you can instruct your audience on installing and setting up your app. This template doesn't rely on any external dependencies or services._

1. Get a free API Key at [https://example.com](https://example.com)
2. Clone the repo
   ```sh
   git clone https://github.com/your_username_/Project-Name.git
   ```
3. Install NPM packages
   ```sh
   npm install
   ```
4. Enter your API in `config.js`
   ```js
   const API_KEY = 'ENTER YOUR API';
   ``` -->

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

### Screenshots
(Explanations available only in Hebrew)  
  
![image](https://user-images.githubusercontent.com/69516798/148556573-c570a299-497a-4d2e-a5d1-3830c1cc4e5d.png) 
![image](https://user-images.githubusercontent.com/69516798/148556697-020e19f9-472b-4198-9a5b-8ff8bd95e5c5.png)  
![image](https://user-images.githubusercontent.com/69516798/148556825-cb576782-8c2b-4497-895d-3791d0dcd3c8.png)  
![image](https://user-images.githubusercontent.com/69516798/148556840-b2899de9-a0cd-4dac-8bde-ca01e23fad2e.png)  
![image](https://user-images.githubusercontent.com/69516798/148556859-30c9d756-c44d-4a6b-98e2-10d01f50e4b8.png)  
![image](https://user-images.githubusercontent.com/69516798/148556875-a9ce4be3-8b6d-4593-86b3-03f90dd7c320.png)  
![image](https://user-images.githubusercontent.com/69516798/148556889-3c0683e2-6625-4302-8b41-a8b55f00807a.png)
![image](https://user-images.githubusercontent.com/69516798/148556895-f4b25a3d-a2b0-4fc1-98e1-fb31239ea393.png)

_For more examples, please refer to the [Demo](https://youtu.be/4ylUOvWFy60)_

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap

- [x] Finished part 1
- [x] Finished part 2
- [x] Finished part 3
- [x] Add a demo
- [x] Finish working on README

See the [open issues](https://github.com/erezhasson/SmartSend/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

**NOTE:** This project is an academic project which already submitted, although contributions is always appreciated I won't be able to resubmit this project. 
Feel free to send me general thoughts or suggestions for future projects.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Erez Hasson - [Portfolio](https://portfolio-erezhasson.web.app/) - erezhasson4@gmail.com

Project Link: [https://github.com/erezhasson/SmartSend](https://github.com/erezhasson/SmartSend)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

Use this space to list resources you find helpful and would like to give credit to. I've included a few of my favourites to kick things off!

* [Choose an Open Source License](https://choosealicense.com)
* [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
* [Img Shields](https://shields.io)
* [GitHub Pages](https://pages.github.com)
* [Font Awesome](https://fontawesome.com)
<!-- * [React Icons](https://react-icons.github.io/react-icons/search) -->

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/erezhasson/SmartSend.svg?edge_flat=false
[contributors-url]: https://github.com/erezhasson/SmartSend/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/erezhasson/SmartSend.svg?edge_flat=false
[forks-url]: https://github.com/erezhasson/SmartSend/network/members
[stars-shield]: https://img.shields.io/github/stars/erezhasson/SmartSend.svg?edge_flat=false
[stars-url]: https://github.com/erezhasson/SmartSend/stargazers
[issues-shield]: https://img.shields.io/github/issues/erezhasson/SmartSend.svg?edge_flat=false
[issues-url]: https://github.com/erezhasson/SmartSend/issues
[license-shield]: https://img.shields.io/github/license/erezhasson/SmartSend.svg?edge_flat=false
[license-url]: https://github.com/erezhasson/SmartSend/blob/main/LICENSE
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?edge_flat=false&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/erezhasson
[product-video]: https://user-images.githubusercontent.com/69516798/148689316-66bae96c-8ee8-4245-9afd-6dd25fd5f793.mp4






