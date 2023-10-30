package kusitms.gallae.dto.program;


import lombok.Data;

import java.util.List;

@Data
public class ProgramMainRes {

    private Long id;

    private String photoUrl;

    private String programName;

    private Long Like;

    private String remainDay;

    private List<String> hashTag;

}
