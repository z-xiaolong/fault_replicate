import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZDH on 2017/7/19.
 */
public class TestFlowOne {
    private WebDriver driver;
    private String trainType;//0--all,1--GaoTie,2--others
    private String baseUrl;
    public static void login(WebDriver driver,String username,String password){
        driver.findElement(By.id("flow_one_page")).click();
        driver.findElement(By.id("flow_preserve_login_email")).clear();
        driver.findElement(By.id("flow_preserve_login_email")).sendKeys(username);
        driver.findElement(By.id("flow_preserve_login_password")).clear();
        driver.findElement(By.id("flow_preserve_login_password")).sendKeys(password);

        try{
            //Add random delay to emulate the waiting between user click.
            Thread.sleep(new Random().nextInt(6000));
        }catch (Exception e){

        }
        driver.findElement(By.id("flow_preserve_login_button")).click();
    }
    public static String getRandomString(int length) {
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb = new StringBuffer();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }
    @BeforeClass
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", "D:/Program/chromedriver_win32/chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "http://10.141.212.22/";
        trainType = "1";//all
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }
    @Test
    //Test Flow Preserve Step 1: - Login
    public void testLogin()throws Exception{

        driver.get(baseUrl + "/");

        //define username and password
        String username = "fdse_microservices@163.com";
        String password = "DefaultPassword";

        //call function login
        login(driver,username,password);
        Thread.sleep(5000);

        //get login status
        String statusLogin = driver.findElement(By.id("flow_preserve_login_msg")).getText();
        if("".equals(statusLogin))
            System.out.println("Failed to Login! Status is Null!");
        else if(statusLogin.startsWith("Success"))
            System.out.println("Success to Login! Status:"+statusLogin);
        else
            System.out.println("Failed to Login! Status:"+statusLogin);
        Assert.assertEquals(statusLogin.startsWith("Success"),true);
    }
    @Test (dependsOnMethods = {"testLogin"})
    //test Flow Preserve Step 2: - Ticket Booking
    public void testBooking() throws Exception{

        //Add random delay to emulate the waiting between user click.
        Thread.sleep(new Random().nextInt(7000) + 3000);

        //locate booking startingPlace input
        WebElement elementBookingStartingPlace = driver.findElement(By.id("travel_booking_startingPlace"));
        elementBookingStartingPlace.clear();
        elementBookingStartingPlace.sendKeys("Shang Hai");

        //locate booking terminalPlace input
        WebElement elementBookingTerminalPlace = driver.findElement(By.id("travel_booking_terminalPlace"));
        elementBookingTerminalPlace.clear();
        elementBookingTerminalPlace.sendKeys("Su Zhou");

        //locate booking Date input
        String bookDate = "";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar newDate = Calendar.getInstance();
        Random randDate = new Random();
        int randomDate = randDate.nextInt(26);
        newDate.add(Calendar.DATE, randomDate+5);
        bookDate=sdf.format(newDate.getTime());

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('travel_booking_date').value='"+bookDate+"'");


        //locate Train Type input
        WebElement elementBookingTraintype = driver.findElement(By.id("search_select_train_type"));
        Select selTraintype = new Select(elementBookingTraintype);
        selTraintype.selectByValue(trainType); //ALL

        //locate Train search button
        WebElement elementBookingSearchBtn = driver.findElement(By.id("travel_booking_button"));


        //Add random delay to emulate the waiting between user click.
        Thread.sleep(new Random().nextInt(6000));
        elementBookingSearchBtn.click();


        Thread.sleep(1000);

        List<WebElement> ticketsList = driver.findElements(By.xpath("//table[@id='tickets_booking_list_table']/tbody/tr"));
        //Confirm ticket selection
        if (ticketsList.size() == 0) {
            elementBookingSearchBtn.click();
            ticketsList = driver.findElements(By.xpath("//table[@id='tickets_booking_list_table']/tbody/tr"));
        }
        if(ticketsList.size() > 0) {
            //Pick up a train at random and book tickets
            System.out.printf("Success to search tickets，the tickets list size is:%d%n",ticketsList.size());
            Random rand = new Random();
            int i = rand.nextInt(1000) % ticketsList.size();
            WebElement elementBookingSeat = ticketsList.get(i).findElement(By.xpath("td[10]/select"));
            Select selSeat = new Select(elementBookingSeat);
            selSeat.selectByValue("3"); //2st


            //Add random delay to emulate the waiting between user click.
            Thread.sleep(new Random().nextInt(6000));
            ticketsList.get(0).findElement(By.xpath("td[13]/button")).click();

            Thread.sleep(1000);
        }
        else
            System.out.println("Tickets search failed!!!");
        Assert.assertEquals(ticketsList.size() > 0,true);
    }
   // @Test(enabled = false)
    @Test (dependsOnMethods = {"testBooking"})
    public void testSelectContacts()throws Exception{

        List<WebElement> contactsList = driver.findElements(By.xpath("//table[@id='contacts_booking_list_table']/tbody/tr"));
        //Confirm ticket selection
        if (contactsList.size() == 0) {


            //Add random delay to emulate the waiting between user click.
            Thread.sleep(new Random().nextInt(6000));
            driver.findElement(By.id("refresh_booking_contacts_button")).click();


            Thread.sleep(1000);
            contactsList = driver.findElements(By.xpath("//table[@id='contacts_booking_list_table']/tbody/tr"));
        }
        if(contactsList.size() == 0)
            System.out.println("Show Contacts failed!");
        Assert.assertEquals(contactsList.size() > 0,true);

        if (contactsList.size() == 1){
            String contactName = getRandomString(5);
            String documentType = "1";//ID Card
            String idNumber = getRandomString(8);
            String phoneNumber = getRandomString(11);
            contactsList.get(0).findElement(By.xpath("td[2]/input")).sendKeys(contactName);

            WebElement elementContactstype = contactsList.get(0).findElement(By.xpath("td[3]/select"));
            Select selTraintype = new Select(elementContactstype);
            selTraintype.selectByValue(documentType); //ID type

            contactsList.get(0).findElement(By.xpath("td[4]/input")).sendKeys(idNumber);//!!
            contactsList.get(0).findElement(By.xpath("td[5]/input")).sendKeys(phoneNumber);//!!

            //Add random delay to emulate the waiting between user click.
            Thread.sleep(new Random().nextInt(6000));
            contactsList.get(0).findElement(By.xpath("td[6]/label/input")).click();//!!
        }

        if (contactsList.size() > 1) {
            Random rand = new Random();
            int i = rand.nextInt(100) % (contactsList.size() - 1);


            //Add random delay to emulate the waiting between user click.
            Thread.sleep(new Random().nextInt(6000));
            contactsList.get(i).findElement(By.xpath("td[7]/label/input")).click();
        }

        //Add random delay to emulate the waiting between user click.
        Thread.sleep(new Random().nextInt(6000));
        driver.findElement(By.id("ticket_select_contacts_confirm_btn")).click();


        System.out.println("Ticket contacts selected btn is clicked");
        Thread.sleep(1000);
    }
    @Test (dependsOnMethods = {"testBooking"})
    public void testTicketConfirm ()throws Exception{

        String itemFrom = driver.findElement(By.id("ticket_confirm_from")).getText();
        String itemTo = driver.findElement(By.id("ticket_confirm_to")).getText();
        String itemTripId = driver.findElement(By.id("ticket_confirm_tripId")).getText();
        String itemPrice = driver.findElement(By.id("ticket_confirm_price")).getText();
        String itemDate = driver.findElement(By.id("ticket_confirm_travel_date")).getText();
        String itemName = driver.findElement(By.id("ticket_confirm_contactsName")).getText();
        String itemSeatType = driver.findElement(By.id("ticket_confirm_seatType_String")).getText();
        String itemDocumentType = driver.findElement(By.id("ticket_confirm_documentType")).getText();
        String itemDocumentNum = driver.findElement(By.id("ticket_confirm_documentNumber")).getText();
        boolean bFrom = !"".equals(itemFrom);
        boolean bTo = !"".equals(itemTo);
        boolean bTripId = !"".equals(itemTripId);
        boolean bPrice = !"".equals(itemPrice);
        boolean bDate = !"".equals(itemDate);
        boolean bName = !"".equals(itemName);
        boolean bSeatType = !"".equals(itemSeatType);
        boolean bDocumentType = !"".equals(itemDocumentType);
        boolean bDocumentNum = !"".equals(itemDocumentNum);
        boolean bStatusConfirm = bFrom && bTo && bTripId && bPrice && bDate && bName && bSeatType && bDocumentType && bDocumentNum;
        if(bStatusConfirm == false){
            driver.findElement(By.id("ticket_confirm_cancel_btn")).click();
            System.out.println("Confirming Ticket Canceled!");
        }


        Assert.assertEquals(bStatusConfirm,true);

        //Add random delay to emulate the waiting between user click.
        Thread.sleep(new Random().nextInt(6000));
        driver.findElement(By.id("ticket_confirm_confirm_btn")).click();
        //Add random delay to emulate the waiting between user click.
        Thread.sleep(new Random().nextInt(6000));
        driver.findElement(By.id("ticket_confirm_confirm_btn")).click();


        Thread.sleep(15000);
        System.out.println("Confirm Ticket!");
        Alert javascriptConfirm = driver.switchTo().alert();
        String statusAlert = driver.switchTo().alert().getText();
        System.out.println("The Alert information of Confirming Ticket："+statusAlert);
        Assert.assertEquals(statusAlert.startsWith("Success"),true);
        javascriptConfirm.accept();

        System.out.println("Confirm Ticket!");
        Alert javascriptConfirm2 = driver.switchTo().alert();
        String statusAlert2 = driver.switchTo().alert().getText();
        System.out.println("The Alert information of Confirming Ticket："+statusAlert2);
        Assert.assertEquals(statusAlert2.startsWith("Success"),true);
        javascriptConfirm2.accept();

    }

    @AfterClass
    public void tearDown() throws Exception {
        driver.quit();
    }
}
