<div id="top"></div>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
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
[![Contributors][contributors-shield]][contributors-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]

<!--
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]
-->


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/TUK-EIS/ADSProject">
    <img src="doc/figures/RPTU_logo.png" alt="Logo" width="400" height="200">
  </a>
  <h3 align="center">
  ADS I Class Project
  <br />
  Group #X
  <br />
  Winter Semester 202X/202X
  </h3>

  <p align="center">
    GitHub repository for Architecture of Digital Systems I Class Project
    <br />
    <br />
    <a href="https://github.com/TUK-EIS/ADSProject/issues">Report Bug</a> 
  </p>
</div>

 ![CI Pipeline](https://github.com/TUK-EIS/ADSProject/actions/workflows/scala.yml/badge.svg?event=push)

<br />

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About the Project

Our lectures [Architecture of Digital Systems I](https://www.eit.uni-kl.de/eis/teaching/85-571) (EIT-EIS-571-V-4) and [Architecture of Digital Systems II](https://www.eit.uni-kl.de/eis/teaching/85-573) (EIT-EIS-573-V-4) deal with the basic principles of computer architecture for single CPU cores and SoCs. To deepen the students' knowledge on computer architecture and to give an impression on how a processor core is implemented, we offer this class project as an add-on to ADS I.

### Chisel - A modern Hardware Description Language

Chisel [^1] (<b>C</b>onstructing <b>H</b>ardware <b>i</b>n a <b>S</b>cala <b>E</b>mbedded <b>L</b>anguage) is an open-source HDL based on the programming language [Scala](https://en.wikipedia.org/wiki/Scala_(programming_language)). It is used to describe digital systems at the register-transfer level (RTL) and is therefore on the same abstraction level as VHDL and (System) Verilog.

Advantages of Chisel over other HDLs are its object-oriented and functional programming characteristics inherited from Scala. This allows a modern coding style and better IDE support compared to VHDL or System Verilog. Additionally, while VHDL and Verilog were introduced as hardware simulators, Chisel is a hardware generator clearly intended to synthezise hardware designs. 

In general, a Chisel design is a scala program describing how to build a specific Chisel graph that represents the hardware design. During compilation and generating of the Chisel graph, severel checks are done in order to guarantee a working circuit. The checks include syntax, invalid circuit designs, unconnected wires, etc. 

Afterwards, the behaviour of the designed circuit can be simulated with [FIRRTL](https://www.chisel-lang.org/firrtl/) (Flexible Internal Representation for RTL). Setting inputs and analyzing the circuit's outputs in simulation can be used to test and validate the behaviour of the design. Unlike other hardware description languages, Chisel is doing checks on whether the design is synthesizable before the simulation is done, during the generation of the Chisel graph. The intermediate representation in FIRRTL can later be translated into System Verilog code which enables FPGA emulation of the circuit design and even the construction of ASICs.

[^1]: Jonathan Bachrach, Huy Vo, Brian Richards, Yunsup Lee, Andrew Waterman, Rimas Avižienis, John Wawrzynek, and Krste Asanović. 2012. [Chisel: constructing hardware in a Scala embedded language](https://dl.acm.org/doi/abs/10.1145/2228360.2228584). In Proceedings of the 49th Annual Design Automation Conference (DAC '12). Association for Computing Machinery, New York, NY, USA, 1216–1225. 








<!-- GETTING STARTED -->
## Getting Started
Fork this repository and follow the instructions to complete the project.
<!--
This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.
-->
### Prerequisites
List of Ubuntu packages required to complete the project:


<!--
This is an example of how to list things you need to use the software and how to install them.
* npm
  ```sh
  npm install npm@latest -g
  ```
-->
<p align="right">(<a href="#top">back to top</a>)</p>

### Installation
1. Clone the repo
   ```sh
   git clone https://github.com/your_username_/ADSProject
   ```

<p align="right">(<a href="#top">back to top</a>)</p>




<!-- ROADMAP -->
## Roadmap
#### Part-1
- [ ] TODO
- [X] DONE
<p align="right">(<a href="#top">back to top</a>)</p>

#### Part-2
- [ ] TODO
- [X] DONE
<p align="right">(<a href="#top">back to top</a>)</p>

#### Part-3
- [ ] TODO
- [X] DONE
<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

<!-- Your Name - [@your_twitter](https://twitter.com/your_username) - email@example.com -->
Tobias Jauch - tobias.jauch@rptu.de

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments
Thanks

<p align="right">(<a href="#top">back to top</a>)</p>

### Project Related Resources

This repo only contains a "warm-up" to get to know the basic functionality of Chisel. If you want to learn more about Chisel, you can start with the material listed below:

* [Official Chisel Website](https://www.chisel-lang.org/)
* [Digital Design with Chisel](http://www.imm.dtu.dk/~masca/chisel-book.pdf), fourth edition, Martin Schoeberl, Kindle Direct Publishing (2019)
* [Chisel Cheat Sheet](https://github.com/freechipsproject/chisel-cheatsheet/releases/latest/download/chisel_cheatsheet.pdf) (a good wrap-up of the base syntax and libraries)

<p align="right">(<a href="#top">back to top</a>)</p>

### Additional Materials

* [README Template](https://github.com/othneildrew/Best-README-Template)
* [WSL](https://docs.microsoft.com/en-us/windows/wsl/install)

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/TUK-EIS/ADSProject.svg?style=for-the-badge
[contributors-url]: https://github.com/TUK-EIS/ADSProject/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/TUK-EIS/ADSProject.svg?style=for-the-badge
[forks-url]: https://github.com/TUK-EIS/ADSProject/network/members
[stars-shield]: https://img.shields.io/github/stars/TUK-EIS/ADSProject.svg?style=for-the-badge
[stars-url]: https://github.com/TUK-EIS/ADSProject/stargazers
[issues-shield]: https://img.shields.io/github/issues/TUK-EIS/ADSProject.svg?style=for-the-badge
[issues-url]: https://github.com/TUK-EIS/ADSProject/issues
<!--
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
-->
[product-screenshot]: images/screenshot.png
