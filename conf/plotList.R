library(ggplot2)

function(size, myScalaList) {
    svg()

    plot <- ggplot(data = data.frame(value = myScalaList, index = 0:size),
                aes(x=index, y=value, group=1)) +
                geom_line(color="blue") +
                expand_limits(x=0, y=0)
    print(plot)
    svg.off()
}