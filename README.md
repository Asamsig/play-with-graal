# Play with GraalVM
Render a graph through R, in a running Scala Play Application.

## How to run the application:

- Install GraalVM either Community Edition or Enterprise Edition
- Export GraalVM to your path
- Install R with `gu install R`
- Install `ggplot2` with `Rscript -e "install.packages(\"ggplot\")"` this takes quite a while
- Install `sbt`
- Run the application with `sbt run`

#### Done:

- Call an R function from Scala
- Pass a single value to R
- Pass a list to R
- Pass a matrix to R
- Pass an R function to Scala

#### TODO:

- Handle multithreading
- Native image






###### Created with Iloydmeta/slim-play G8 template.