lpackage definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import pages.HomePage;

public class GeneralStepdefs {

    HomePage homePage = new HomePage();
    @Given("I open google")
    public void iOpenGoogle() {
        homePage.open();
    }

    @Then("I wait {int} sec")
    public void iWaitSec(int sec) throws InterruptedException {
        Thread.sleep(sec * 1000);
    }
}
