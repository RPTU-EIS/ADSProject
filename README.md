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
<!--[![Contributors][contributors-shield]][contributors-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]-->

 ![CI Pipeline: 01 Warm-Up](https://github.com/RPTU-EIS/ADSProject/actions/workflows/01_warm-up.yml/badge.svg?event=push)
 ![CI Pipeline: 02 Single-Cycle RISC-V Core](https://github.com/RPTU-EIS/ADSProject/actions/workflows/02_single-cycle_RISC-V_core.yml/badge.svg?event=push)

<!--
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]
-->


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/RPTU-EIS/ADSProject">
    <img src="doc/figures/RPTU_logo.png" alt="Logo" width="400" height="200">
  </a>
  <h3 align="center">
  ADS I Class Project
  <br />
  Group #XXX
  <br />
  Winter Semester 202X/202X
  </h3>

  <p align="center">
    GitHub repository for Architecture of Digital Systems I Class Project
    <br />
    <br />
    <a href="https://github.com/RPTU-EIS/ADSProject/issues">Report Bug</a> 
  </p>
</div>

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

Our lectures, [Architecture of Digital Systems I](https://www.eit.uni-kl.de/eis/teaching/85-571) (EIT-EIS-571-V-4) and [Architecture of Digital Systems II](https://www.eit.uni-kl.de/eis/teaching/85-573) (EIT-EIS-573-V-4), deal with the basic principles of computer architecture for single CPU cores and SoCs. To deepen the students' knowledge of computer architecture and provide an impression of how a processor core is implemented, we offer this class project as an add-on to ADS I.

### Chisel - A modern Hardware Description Language

Chisel [^1] (<b>C</b>onstructing <b>H</b>ardware <b>i</b>n a <b>S</b>cala <b>E</b>mbedded <b>L</b>anguage) is an open-source HDL based on the programming language [Scala](https://en.wikipedia.org/wiki/Scala_(programming_language)). It is used to describe digital systems at the register-transfer level (RTL) and is therefore on the same abstraction level as VHDL and (System) Verilog.

Advantages of Chisel over other HDLs are its object-oriented and functional programming characteristics inherited from Scala. This allows a modern coding style and better IDE support compared to VHDL or System Verilog. Additionally, while VHDL and Verilog were introduced as hardware simulators, Chisel is a hardware generator clearly intended to synthezise hardware designs. 

In general, a Chisel design is a scala program describing how to build a specific Chisel graph that represents the hardware design. During compilation and generating of the Chisel graph, severel checks are done in order to guarantee a working circuit. The checks include syntax, invalid circuit designs, unconnected wires, etc. 

Afterwards, the behaviour of the designed circuit can be simulated with [FIRRTL](https://www.chisel-lang.org/firrtl/) (Flexible Internal Representation for RTL). Setting inputs and analyzing the circuit's outputs in simulation can be used to test and validate the behaviour of the design. Unlike other hardware description languages, Chisel is doing checks on whether the design is synthesizable before the simulation is done, during the generation of the Chisel graph. The intermediate representation in FIRRTL can later be translated into System Verilog code which enables FPGA emulation of the circuit design and even the construction of ASICs.

[^1]: Jonathan Bachrach, Huy Vo, Brian Richards, Yunsup Lee, Andrew Waterman, Rimas Avižienis, John Wawrzynek, and Krste Asanović. 2012. [Chisel: constructing hardware in a Scala embedded language](https://dl.acm.org/doi/abs/10.1145/2228360.2228584). In Proceedings of the 49th Annual Design Automation Conference (DAC '12). Association for Computing Machinery, New York, NY, USA, 1216–1225. 

<!-- GETTING STARTED -->
## Getting Started
To take this course you should have a basic knowledge of digital circuits. Here at RPTU, undergrad courses teaching those topics are for example [Grundlagen der Informationsverarbeitung](https://www.eit.uni-kl.de/eis/teaching/85-314) and [Labor Digitaltechnik I](https://www.kis.uni-kl.de/campus/all/event.asp?gguid=0xCC211C8A651847DE8DF5645FE17064D4&tguid=0x8054315EB9314F5A9AB49FBCBE1D5705). If you got your Bachelor's degree from another university, you probably took similar courses.

### Prerequisites
In general, it is best to use a machine running Linux Ubuntu 20.04 or newer for this course. Other Linux distributions, Windows (with Linux subsystem) or macOS might be suitable as well, but if you want to use them you need to figure out how to install and build everything for yourself, if the provided instructions don't work.

#### An Editor / IDE for Chisel
We recommend using an integrated development environment (IDE) as an editor for coding. IDEs provide helpful functions like the integration of version control systems (e.g. Git) or syntax highlighting and other helpful features to keep a better overview over big code bases.  In case you already have worked with an IDE that you favor, feel free to use it. If this area is new to you, IntelliJ is a professional IDE that also provides support for Chisel. As a student, you can get a free license [here](https://www.jetbrains.com/community/education/#students).

#### Java / Scala / sbt
First of all, as Scala (the underlying programming language of Chisel) uses the java virtual machine (JVM), your workspace must be able to run java. Afterward, you need some more tools like the Scala compiler (scalac), the Scala build tool (sbt), and some others, to use all features of Chisel. Luckily, Scala provides all of these tools in one combined script. You just need to execute the following command in your terminal (on Linux) and the script will automatically check if you have some of the tools already present and install the missing ones.
```sh
curl -fL https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz | gzip -d > cs && chmod +x cs && ./cs setup
```
If you need more information, just check the official [scala install page](https://docs.scala-lang.org/getting-started/index.html#install-scala-on-your-compute).

#### Git Version Control
Git is the most common version control system in the world today. GitHub, on the other hand, is a cloud-based service that offers a platform for storing and managing projects using Git. Generally, working with GitHub is based on some simple principles. There are some very complex commands, too, but you usually won't need them in your everyday work with Git. To install Git, you can use the following command:
```sh
sudo apt install git-all
```
<p align="right">(<a href="#top">back to top</a>)</p>

### How to Git
![Git](https://imgs.xkcd.com/comics/git.png)

1. Forking the Main Repository
* Click on the "Fork" button at the top-right corner of the page.
* This will create a copy of the repository under your GitHub account.
 
2. Cloning the Forked Repository
* Open a terminal or command prompt on your local machine.
* Navigate to the directory where you want to clone the repository.
* Run the following command, replacing <your-account> with the name of your GitHub account:
```sh
git clone https://github.com/<your-account>/ADSProject
```

3. Creating a New Branch
* Change into the cloned repository directory.
* Run the following command to create a new branch that starts from the exact state as the main branch you're currently on.
* Replace 'XXX' with your group number:
```sh
git checkout -b <GroupXXX>
```

4. Pushing the New Branch to your Fork on GitHub
* Your forked repository on GitHub doesn't know about the new branch you created, yet.
* Push the new branch to your repository with the following command:
'''git push --set-upstream origin <your branch name>
'''

5. Pushing Changes to the New Branch}
* Make the desired changes to the files in your local repository.
* Run the following commands to stage and commit your changes:
```sh
git add <file-you-want-to-add>  // "add ." will add all new files
```
```sh
git commit -a -m "Your commit message"
```
* Finally, push your changes to your repository using the following command:
```sh
git push
```
Whenever you want to add, commit and push changes to your repo, you can do this by repeating step 5. If multiple people work in the same directory, you should always pull the changes other people pushed recently to avoid conflicts (e.g., by editing the same region of the same file simultaneously). Getting the latest state is simply done by using \textit{git pull}:
```sh
git pull
```

That's it! You have now successfully forked a repository, cloned it, created a new branch, and pushed your changes to the new branch on GitHub.

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->
## Contact
Tobias Jauch - jauch@rptu.de

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->
<!--## Acknowledgments
Thanks to XY

<p align="right">(<a href="#top">back to top</a>)</p>-->

### Additional Materials

If you want to learn more about Chisel, you can start with the material listed below:

* [Official Chisel Website](https://www.chisel-lang.org/)
* [Getting Started with Chisel](https://inst.eecs.berkeley.edu/~cs250/sp16/handouts/chisel-getting-started.pdf)
* [Digital Design with Chisel](http://www.imm.dtu.dk/~masca/chisel-book.pdf), fourth edition, Martin Schoeberl, Kindle Direct Publishing (2019)
* [Chisel Cheat Sheet](https://github.com/freechipsproject/chisel-cheatsheet/releases/latest/download/chisel_cheatsheet.pdf) (a good wrap-up of the base syntax and libraries)

If you want to set up a Linux subsystem on your Windows PC, you can find information on how to do so below:
* [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install)

<p align="right">(<a href="#top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/RPTU-EIS/ADSProject.svg?style=for-the-badge
[contributors-url]: https://github.com/RPTU-EIS/ADSProject/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/RPTU-EIS/ADSProject.svg?style=for-the-badge
[forks-url]: https://github.com/RPTU-EIS/ADSProject/network/members
[stars-shield]: https://img.shields.io/github/stars/RPTU-EIS/ADSProject.svg?style=for-the-badge
[stars-url]: https://github.com/RPTU-EIS/ADSProject/stargazers
[issues-shield]: https://img.shields.io/github/issues/RPTU-EIS/ADSProject.svg?style=for-the-badge
[issues-url]: https://github.com/RPTU-EIS/ADSProject/issues
<!--
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
-->
[product-screenshot]: images/screenshot.png
