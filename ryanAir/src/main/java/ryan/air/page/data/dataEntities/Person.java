package ryan.air.page.data.dataEntities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import lombok.Data;
import ryan.air.page.customPages.PaymentPage;

import java.util.Random;

/**
 * object to satore data about people
 * Created by MVostrikov on 21.09.2016.
 */
@Data
public class Person {


    @XStreamAlias("title")
    @XStreamAsAttribute
    protected String title;

    @XStreamAlias("firstName")
    @XStreamAsAttribute
    protected String firstName;

    @XStreamAlias("firstSurname")
    @XStreamAsAttribute
    protected String firstSurname;

    @XStreamAlias("email")
    @XStreamAsAttribute
    protected String email;

    @XStreamAlias("country")
    @XStreamAsAttribute
    protected String country;

    @XStreamAlias("phone")
    @XStreamAsAttribute
    protected String phone;

    @XStreamAlias("address1")
    @XStreamAsAttribute
    protected String address1;

    @XStreamAlias("address2")
    @XStreamAsAttribute
    protected String address2;

    @XStreamAlias("city")
    @XStreamAsAttribute
    protected String city;

    @XStreamAlias("zip")
    @XStreamAsAttribute
    protected String zip;

    public static Random rnd = new Random(System.currentTimeMillis()); //Todo I know, that it is a bad style to store random. But this is an only place we use it. So have't desided best place to store utility class with it

    /**
     * generation of random person
     * @return
     */
    public static Person generateNewPerson() {
        Person newPerson = new Person();

        newPerson.title = rnd.nextBoolean()? "Mrs":"Mr";
        newPerson.firstName = generateString(6); //Todo best way is to use maximum field lengh
        newPerson.firstSurname = generateString(8);
        newPerson.email = newPerson.firstName + "@" + newPerson.firstSurname + ".com";
        newPerson.country = "Andorra"; //Todo can make this random too. Need time to investigate this further
        newPerson.phone = generateNumber(9);
        newPerson.address1 = generateString(10);
        newPerson.address2 = generateString(10);
        newPerson.city = generateString(6);
        newPerson.zip = generateNumber(6);

        return newPerson;
    }

    /**
     * generate random string. As rnd - it is bad to store it here.
     */
    private static String generateString(int size) {
        StringBuilder str = new StringBuilder();
        str.append((char)(rnd.nextInt(26) + 'A')); //first is Uppercase
        for (int i=1; i<size; i++) { //if size<1 - will still return 1 capital char string
            str.append((char)(rnd.nextInt(26) + 'a'));
        }
        return str.toString();
    }
    /**
     * generate random string with numbers. As rnd - it is bad to store it here.
     */
    private static String generateNumber(int size) {
        StringBuilder str = new StringBuilder();
        for (int i=0; i<size; i++) {
            str.append(Integer.toString(rnd.nextInt(10)));
        }
        return str.toString();
    }

    /**
     * fill in data on page.
     * Posibly it is a good way to store this in PaymentPage class, but I think it is better to store all code about Person in one place
     */
    public void fillInPesonDataOnPaymentPage(PaymentPage page) throws Exception {
        page.fillSelectByLebelText("Title", title);
        page.fillTextFieldByLebelText("First name", firstName);
        page.fillTextFieldByLebelText("First surname", firstSurname);
        page.fillTextFieldByLebelText("Email address (Booking confirmation)", email);
        page.fillTextFieldByLebelText("Confirm email address", email);

        page.fillSelectByLebelText("Phone number", country);
        page.fillTextFieldByLebelText("Phone number", phone);

        page.fillTextFieldByLebelText("Address 1", address1);
        page.fillTextFieldByLebelText("Address 2", address2);
        page.fillTextFieldByLebelText("City", city);

        page.fillTextFieldByLebelText("Postcode/ZIP code", zip);

        page.fillTextFieldByLebelText("Cardholder's name", firstName + " " + firstSurname);

    }

}
