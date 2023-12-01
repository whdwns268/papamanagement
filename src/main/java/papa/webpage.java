package papa;

import com.google.gson.Gson;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class webpage {

    PrintWriter pw = null;

    @Inject
    private SqlSessionTemplate sqlSession;

    @RequestMapping("/test")
    public void test(){
    }
    @CrossOrigin(origins = "*")
    @RequestMapping("/qnajsonset.do")
    public void qnajsonset(@ModelAttribute("board") QnaCustomerVO QnaCustomerVO,
                       HttpServletRequest req, HttpServletResponse res,
                       @RequestBody Map<String, String> data) {

        res.setCharacterEncoding("UTF-8");
        //res.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");


        String gsmethod = data.get("gsmethod");
        System.out.printf("gsmethod : " + gsmethod);

        String sheetdata = data.get("sheetdata");
        String qidx = data.get("qidx");
        String category = data.get("category");
        String category_detail = data.get("category_detail");
        String detail = data.get("detail");
        String preface = data.get("preface");
        String lastupdate = data.get("lastupdate");

        String shdata = "";
        if(sheetdata.equals("고객문의")){
            shdata = "Qna_customer";
        }else if(sheetdata.equals("크루문의")) {
            shdata = "Qna_crew";
        }


        System.out.printf(gsmethod);
        System.out.printf(sheetdata);


        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("shdata", shdata);
        paramMap.put("qidx", qidx);
        paramMap.put("category",category);
        paramMap.put("category_detail", category_detail);
        paramMap.put("detail", detail);
        paramMap.put("preface", preface);
        paramMap.put("lastupdate", lastupdate);
        System.out.printf(paramMap.toString());

        if (gsmethod.equals("gsget")) {
            try {

                List<QnaCustomerVO> nc2 = sqlSession.selectList("papaDB.papa_qnaCustomerVO", paramMap);
                Gson gson = new Gson();
                String json = gson.toJson(nc2);

                PrintWriter pw = res.getWriter();

                pw.print(json);
                pw.flush();
                pw.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        신규데이터추가
        else if(gsmethod.equals("gspost")){
            int r = sqlSession.insert("papaDB.Qnainsert",paramMap);
            System.out.printf(String.valueOf(r));
            if(r>0){
                System.out.printf("완료");
            }else{
                System.out.printf("실패");
            }
        }
        else if(gsmethod.equals("gsupdate")){
            int r = sqlSession.insert("papaDB.Qnaupdate",paramMap);
            System.out.printf(String.valueOf(r));
            if(r>0){
                System.out.printf("완료");
            }else{
                System.out.printf("실패");
            }
        }
        else if(gsmethod.equals("gsdelete")){
            int r = sqlSession.delete("papaDB.Qnadelete",paramMap);
            System.out.printf(String.valueOf(r));
            if(r>0){
                System.out.printf("완료");
            }else{
                System.out.printf("실패");
            }
        }
    }


//        @RequestMapping("/qnajsonset.do")
//    public void index1(@ModelAttribute("board") QnaCustomerVO QnaCustomerVO,HttpServletRequest req, HttpServletResponse res ) {
//            res.setCharacterEncoding("UTF-8");
//            res.addHeader("Access-Control-Allow-Origin", "*");
//            res.addHeader("Access-Control-Allow-Credentials", "true");
//
//            List<QnaCustomerVO> nc2 = sqlSession.selectList("papaDB.papa_qnaCustomerVO", QnaCustomerVO);
//            JSONArray array = new JSONArray();
//
//            for(QnaCustomerVO datas : nc2) {
//                array.add(datas.getQidx());
//                array.add(datas.getCategory());
//                array.add(datas.getCategory_detail());
//                array.add(datas.getDetail());
//                array.add(datas.getPreface());
//                array.add(datas.getLastupdate());
//            }
//            try {
//                PrintWriter pw = res.getWriter();
//                pw.print(array);
//                pw.flush();
//                pw.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//    }

    @CrossOrigin(origins = "*")
    @RequestMapping( "/sheetdataapi.do")
    public ResponseEntity<String> google_sheetpost(@RequestBody Map<String, String> data) {
        try {
            String gsmethod = data.get("gsmethod");
            System.out.printf("gsmethod : " + gsmethod);
            GoogleSheets gsheet = new GoogleSheets(0);

            if (gsmethod.equals("gspost")) {
                gsheet.gs_post(data);
            } else if (gsmethod.equals("gsupdate")) {
                gsheet.gs_update(data);
            } else if (gsmethod.equals("gsget")) {
                gsheet.gs_get(data);

                String jsonData = gsheet.sheetget(data);

                Gson gson = new Gson();
                String jsonDataList = gson.toJson(jsonData);

                // HTTP 응답 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                //System.out.printf(jsonDataList);
                // ResponseEntity를 사용하여 JSON 응답 반환
                return new ResponseEntity<>(jsonDataList, headers, HttpStatus.OK);

            } else if (gsmethod.equals("gsdelete")) {
                gsheet.gs_delete(data);

            } else if (gsmethod.equals("gssearch")) {
                gsheet.gs_search(data);

            } else {
                System.out.println("gsmethod 확인필요");
            }

            System.out.println("ok");
            return ResponseEntity.ok("{\"message\": \"ok\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Internal Server Error\"}");
        }
    }
}
