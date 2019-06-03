# DCT LSB Image Steganography Project
It is a full-fledged JavaFX Application for hiding text messages in JPEG image. The technique is known as [Steganography](https://en.wikipedia.org/wiki/Steganography) <br /><br />
**This project is the implementation of my own research paper**. The corresponding paper can be found [here](http://kec.edu.np/a-secure-and-effective-pattern-based-steganographic-method-in-coloured-jpeg-images/).

## Installing
### Pre-requisites
1. <b>MySQL Server</b>
 
    The confuguration of MySQL Server is:
    ```mysql
    **server**: localhost
    port: 3306
    username: root
    password: none
    ```
    You can use the above credentials, or use you own.
    Modify the *DbUtil.java* file as necessary. The file is located in src->util
    
2. **Java Development Kit 8**

    You can download the JDK from [here](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
    
Launch the Application by running *MainWindow.java* located in the *src* folder.

## Acknowledgements
I would like to thank all the professionals in the field for their contribution.
I am grateful to the maintainers of the following libraries:
1. [JFoeniX](http://www.jfoenix.com/)
2. [ControlsFX](https://github.com/controlsfx/controlsfx)
3. [Apache Commons Codec](https://commons.apache.org/proper/commons-codec/)

## NOTE
I developed the project in IntelliJ IDEA. You can use the *impl* file for configurations.

### Have a Nice Day!
*Gaurav Subedi*