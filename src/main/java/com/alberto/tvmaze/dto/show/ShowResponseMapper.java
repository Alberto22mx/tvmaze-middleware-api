package com.alberto.tvmaze.dto.show;

import com.alberto.tvmaze.dto.comment.CommentSummaryResponse;
import com.alberto.tvmaze.dto.show.external.TvMazeShowDetails;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ShowResponseMapper {

    public ShowResponse toResponse(TvMazeShowDetails show, List<CommentSummaryResponse> comments) {
        return new ShowResponse(
                show.id(),
                show.url(),
                show.name(),
                show.type(),
                show.language(),
                show.genres(),
                show.status(),
                show.runtime(),
                show.averageRuntime(),
                show.premiered(),
                show.ended(),
                show.officialSite(),
                show.schedule(),
                show.rating(),
                show.weight(),
                show.network(),
                show.webChannel(),
                show.dvdCountry(),
                show.externals(),
                show.image(),
                show.summary(),
                show.updated(),
                show._links(),
                comments);
    }
}
