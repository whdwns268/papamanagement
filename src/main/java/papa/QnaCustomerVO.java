package papa;

import lombok.Getter;
import lombok.Setter;
import java.util.Date; // java.util.Date를 임포트해야 합니다.

@Getter
@Setter
public class QnaCustomerVO {
    int qidx;
    String category , category_detail , detail , preface;
    Date lastupdate;
}
