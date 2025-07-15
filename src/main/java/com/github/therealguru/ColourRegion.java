package com.github.therealguru;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@RequiredArgsConstructor
@Data
public class ColourRegion {

    private final Coordinate from;
    private final Coordinate to;
    private final Color color;
}
