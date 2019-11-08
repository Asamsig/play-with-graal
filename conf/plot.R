library(ggplot2)
data <<- numeric(100)

function(value) {
    svg()

    data <<- c(data[2:100], value)

    plot <- ggplot(data = data.frame(value = data, time = -99:0),
                aes(x=time, y=value, group=1)) +
                geom_line(color="blue") +
                expand_limits(x=0, y=0)
    print(plot)
    svg.off()
}