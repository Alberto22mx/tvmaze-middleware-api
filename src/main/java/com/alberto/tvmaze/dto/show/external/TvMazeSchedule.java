package com.alberto.tvmaze.dto.show.external;

import java.util.List;

public record TvMazeSchedule(String time, List<String> days) {
}
