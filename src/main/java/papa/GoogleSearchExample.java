package papa;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class GoogleSearchExample {
    public static void main(String[] args) {
        // ChromeDriver 경로 설정
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");

        // Chrome 브라우저 시작
        WebDriver driver = new ChromeDriver();

        // 구글 웹사이트 열기
        driver.get("https://www.google.com");

        // 검색 입력 상자를 찾아 검색어 입력
        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.sendKeys("Selenium Webdriver");

        // 검색 버튼 클릭
        WebElement searchButton = driver.findElement(By.name("btnK"));
        searchButton.click();

        // 검색 결과 가져오기
        WebElement searchResults = driver.findElement(By.id("search"));

        // 결과 출력
        System.out.println(searchResults.getText());

        // 브라우저 닫기
        driver.quit();
    }
}
