package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

public class LineRequest {

    private String name;

    public String getName() {
        return name;
    }

    public Line toLine() {
        return new Line(name);
    }
}
