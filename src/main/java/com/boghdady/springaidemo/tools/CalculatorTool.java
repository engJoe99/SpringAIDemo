package com.boghdady.springaidemo.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CalculatorTool {

    @Tool(description = "Add two numbers and return the result")
    public double add(@ToolParam(description = "The first number") double a,
                      @ToolParam(description = "The second number") double b) {
        log.info("add: {} + {}", a, b);
        return a + b;
    }

    @Tool(description = "Multiply two numbers and return the result")
    public double multiply(@ToolParam(description = "The first number") double a,
                           @ToolParam(description = "The second number") double b) {
        log.info("multiply: {} * {}", a, b);
        return a * b;
    }

    @Tool(description = "Raise a number to a power and return the result")
    public double power(@ToolParam(description = "The base number") double a,
                       @ToolParam(description = "The power number") double b) {
        log.info("power: {} ^ {}", a, b);
        return Math.pow(a, b);
    }

    @Tool(description = "Calculate the percentage of a number")
    public double percentage(@ToolParam(description = "The number to calculate the percentage of") double a,
                            @ToolParam(description = "The percentage to calculate") double b) {
        log.info("percentage: {}% of {}", b, a);
        return a * b / 100;
    }

}
