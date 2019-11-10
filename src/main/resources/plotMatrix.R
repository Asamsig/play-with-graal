library(ggplot2)

function(size, myScalaMatrix) {
    svg()

    my_data <- data.frame(myScalaMatrix)

    plot <- ggplot(data = my_data,
                aes_string(x=names(my_data)[1])) +
                geom_histogram(bins=size)
    print(plot)
    svg.off()
}