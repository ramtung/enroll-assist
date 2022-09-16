package ir.proprog.enrollassist.controller.section;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import ir.proprog.enrollassist.domain.entity.Section;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@XStreamAlias("SectionList")
public class SectionListXmlDTO {
    private List<SectionXmlDTO> sections;

    public SectionListXmlDTO(List<Section> sections) {
        this.sections = new ArrayList<>();
        sections.forEach(o -> this.sections.add(new SectionXmlDTO(o)));
    }
}
