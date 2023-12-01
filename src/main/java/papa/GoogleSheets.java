package papa;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.Credentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GoogleSheets {

    public void main(String[] args) {

    }

    public String gs_get(Map<String, String> data) {
        try {
            GoogleSheets googleSheets = new GoogleSheets(sheetId);
            String jsonData = googleSheets.sheetget(data); // sheetget의 반환 값을 저장
            return jsonData; // 저장된 jsonData 반환
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"An error occurred.\"}";
        }
    }

    int sheetId=0;
    int sheetId_ok=0;

    public void gs_update(Map<String, String> data) {
        GoogleSheets googleSheets = new GoogleSheets(sheetId);
        googleSheets.sheetupdate(data);
    }

    public void gs_post(Map<String, String> data) {
        GoogleSheets googleSheets = new GoogleSheets(sheetId);
        googleSheets.sheetpost(data);
    }

    public void gs_search(Map<String, String> data) throws IOException {
        GoogleSheets googleSheets = new GoogleSheets(sheetId);
        googleSheets.sheetsearch(data);
    }


    public void gs_delete(Map<String, String> data) {

        String sheetdata = data.get("sheetdata");

        if(sheetdata.equals("customer")){
            sheetId = 1;
        } else if (sheetdata.equals("crew")){
            sheetId = 2;
        }

        GoogleSheets googleSheets = new GoogleSheets(sheetId);
        googleSheets.sheetdelete(data);




    }

    private static final String APPLICATION_NAME = "Your Application Name";


    private static final String SPREADSHEET_ID = "1NseX3pwI-1kRjKTnB5dYu_1J-5UtmoFokDrz3jjGZJM"; // 스프레드시트 ID
    private static final String RANGE = "예약서비스 관리리스트"; // 시트 이름 (또는 범위)
    private static final String SPREADSHEET_ID2 = "1xO1VVs9_QFkDjzdI-ic0XbGpf9cPbDsyY0TrgPNz4CM"; // 스프레드시트 ID
    private static final String RANGE2 = "Sheet1"; // 시트 이름 (또는 범위)
    private Sheets sheetsService; // sheetsService를 클래스 필드로 선언

    public GoogleSheets(int sheetId) {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            // 서비스 계정 JSON 키 파일 경로
            //String jsonKeyFilePath = "/main/resources/papa-management-c7e222e7017c.json"

            InputStream jsonKeyFilePath = getClass().getClassLoader().getResourceAsStream("papa-management-c7e222e7017c.json");

            Credentials credentials = ServiceAccountCredentials
                    .fromStream(jsonKeyFilePath)
                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/spreadsheets"));

            // Google Sheets 서비스 생성
            sheetsService = new Sheets.Builder(httpTransport, jsonFactory,
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

                    Spreadsheet spreadsheet = sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();
                    sheetId = Integer.valueOf(spreadsheet.getSheets().get(sheetId).getProperties().getSheetId().toString()); // 첫 번째 시트의 ID를 얻음

            this.sheetId_ok = sheetId;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sheetpost(Map<String, String> data) {
        try {

            String sheetdata = data.get("sheetdata");
            String category = data.get("category");
            String detail = data.get("detail");
            String indexdetail = data.get("indexdetail");
            String preface = data.get("preface");
            System.out.printf(data.toString());
            String lastupdate = data.get("lastupdate");
            System.out.printf("ckck");
            System.out.printf(data.toString());
            String SheetRANGE = null;
            if (sheetdata.equals("crew")) {
                SheetRANGE = "(QNA)crew";
            } else if (sheetdata.equals("customer")) {
                SheetRANGE = "(QNA)customer";
            } else {
                System.out.printf("sheetdata 확인필요");
            }

            //시트 마지막 행 번호 가져오기
            getLastRow(SheetRANGE);
            int lastRow = getLastRow(SheetRANGE);
            System.out.printf("\nlastRow : " + lastRow);
            List<List<Object>> newData = Arrays.asList(
                    Arrays.asList(lastRow + 1, category, detail, indexdetail, preface, lastupdate)
            );

            ValueRange body = new ValueRange().setValues(newData);

            AppendValuesResponse result = sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, SheetRANGE, body)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS") // 행 추가 옵션 (데이터를 삽입하고 싶은 방법 지정)
                    .execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sheetupdate(Map<String, String> data) {

        String sheetdata = data.get("sheetdata");
        if(sheetdata.equals("customer")){
            sheetdata = "(QNA)customer";
        } else if (sheetdata.equals("crew")){
            sheetdata = "(QNA)crew";
        }
        String no = data.get("no");
        int number = Integer.parseInt(no);

        String category = data.get("category");
        String detail = data.get("detail");
        String indexdetail = data.get("indexdetail");
        String preface = data.get("preface");
        String lastupdate = data.get("lastupdate");

        try {
            int rannumber = number + 1;
            String range = sheetdata+"!A"+rannumber;
            System.out.printf(range);
            // 수정할 데이터 준비
            List<List<Object>> updateData = Arrays.asList(
                    Arrays.asList(no,category,detail,indexdetail,preface,lastupdate)
            );


            ValueRange body = new ValueRange().setValues(updateData);

            UpdateValuesResponse result = sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, range, body)
                    .setValueInputOption("RAW")
                    .execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sheetdelete(Map<String, String> data) {

        System.out.println("sheetId: " + sheetId_ok);


        try {
            //삭제할 행
            String no = data.get("no");
            int rannumber = Integer.parseInt(no);

            Request deleteRequest = new Request()
                    .setDeleteDimension(new DeleteDimensionRequest()
                            .setRange(new com.google.api.services.sheets.v4.model.DimensionRange()
                                    .setSheetId(sheetId_ok)
                                    .setDimension("ROWS")
                                    .setStartIndex(rannumber)
                                    .setEndIndex(rannumber + 1)));

            List<Request> requests = new ArrayList<>();
            requests.add(deleteRequest);

            // Execute the batch update to delete the row
            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, body).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sheetsearch(Map<String, String> data) throws IOException {
        try {
            // 날짜와 이름으로 찾을 값을 설정
            String searchDate = "2023-10-16"; // 원하는 날짜
            String searchName = "문종준"; // 원하는 이름

            // 값을 읽어올 범위 지정 (예: "Sheet1!A1:D10")
            String rangeToRead = "Sheet1!A:D";
            ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, rangeToRead).execute();

            if (response.getValues() != null) {
                for (int rowIndex = 0; rowIndex < response.getValues().size(); rowIndex++) {
                    List<Object> row = response.getValues().get(rowIndex);
                    if (row.size() >= 2) {
                        String dateValue = row.get(0).toString(); // 날짜 열
                        String nameValue = row.get(1).toString(); // 이름 열
                        if (dateValue.equals(searchDate) && nameValue.equals(searchName)) {
                            // 일치하는 행을 찾음
                            System.out.println("Date: " + dateValue + "\tName: " + nameValue);
                            System.out.println("Matching Row Index: " + rowIndex);

                            String newValue = "test value";
                            // 업데이트할 열의 인덱스 지정 (예: 2번째 열, 0부터 시작)
                            int columnIndexToUpdate = 2; // 예시로 2번째 열로 설정
                            List<List<Object>> newValues = new ArrayList<>();
                            List<Object> newRow = new ArrayList<>();
                            newRow.add(newValue);
                            newRow.add(newValue);
                            newRow.add(newValue);// 업데이트할 새로운 값
                            newValues.add(newRow);

                            // 업데이트할 범위 지정
                            String rangeToWrite = "Sheet1!C" + (rowIndex + 1) + ":" + "F" + (rowIndex + 1); // 예시로 3번째 열로 업데이트
                            ValueRange requestBody = new ValueRange().setValues(newValues);
                            sheetsService.spreadsheets().values().update(SPREADSHEET_ID, rangeToWrite, requestBody)
                                    .setValueInputOption("RAW")
                                    .execute();
                            System.out.println("Value updated!");
                        }
                    }
                }
            } else {
                System.out.println("No data found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sheetget(Map<String, String> data) {
        try {
            String sheetdata = data.get("sheetdata");
            System.out.printf("sheetdata : " + sheetdata);
            String range = null;
            if (sheetdata.equals("고객문의")) {
                range = "(QNA)customer";
            } else if (sheetdata.equals("크루문의")) {
                range = "(QNA)crew";
            }
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, RANGE)
                    .execute();

            List<List<Object>> values = response.getValues();
            int lastRow = values != null ? values.size() : 0;

            if (values == null || values.size() < 2) {
                System.out.println("No data found.");
                return ""; // 어떤 기본값이나 빈 문자열을 반환
            } else {

                int startColIndex = 0;  // A열의 인덱스 (0부터 시작)
                int endColIndex = 30;
                List<Object> categories = values.get(0); // 첫 번째 행을 카테고리로 사용

                List<JsonObject> jsonDataList = new ArrayList<>();
                for (int i = 1; i < values.size(); i++) {
                    List<Object> rowData = values.get(i); // 나머지 행을 데이터로 처리

                    JsonObject jsonData = new JsonObject();
                    for (int j = startColIndex; j <= endColIndex && j < rowData.size(); j++) {
                        String category = categories.get(j).toString(); // 카테고리 가져오기
                        String value = rowData.get(j).toString(); // 해당 셀의 값 가져오기
                        jsonData.addProperty(category, value);
                    }

                    jsonDataList.add(jsonData);
                }

                // Convert data to JSON
                Gson gson = new Gson();
                String json = gson.toJson(jsonDataList);
                return json; // JSON 데이터를 반환
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ""; // 예외 발생 시 어떤 기본값이나 빈 문자열을 반환
        }
    }


    class Data {
        private String category;
        private String value;

        public Data(String category, String value) {
            this.category = category;
            this.value = value;
        }
    }

    private int getLastRow(String SheetRANGE) {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, SheetRANGE)
                    .execute();

            List<List<Object>> values = response.getValues();
            return values != null ? values.size() : 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0; // 오류 발생 시 0을 리턴
        }
    }
}
