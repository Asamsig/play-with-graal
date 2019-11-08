library(ggplot2)

function(size, myScalaList) {
    svg()

    plot <- ggplot(data = data.frame(value = myScalaList, index = 0:size),
                aes(x=value)) +
                geom_density(fill="#69b3a2", color="#e9ecef", alpha=0.8) +
                expand_limits(x=0, y=0)
    print(plot)
    svg.off()
}