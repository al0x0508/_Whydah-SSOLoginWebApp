package net.whydah.sso.usertoken;

public class ApplicationCredential {
    private String applicationID="apphkjhkjhkjh";
    private String applicationPassord="nmnmnm,n,";

    private final String templateToken = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
            "<template>\n" +
            "    <applicationcredential>\n" +
            "        <params>\n" +
            "            <applicationID>" + getApplicationID() + "</applicationID>\n" +
            "            <applicationSecret>" + getApplicationPassord() + "</applicationSecret>\n" +
            "        </params> \n" +
            "    </applicationcredential>\n" +
            "</template>";



    public String toXML(){
        if (applicationID == null){
            return templateToken;
        } else {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
            "<applicationcredential>\n" +
            "    <params>\n" +
            "        <applicationID>" + getApplicationID() + "</applicationID>\n" +
            "        <applicationSecret>" + getApplicationPassord() + "</applicationSecret>\n" +
            "    </params> \n" +
            "</applicationcredential>\n" ;
        }
    }


    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }
    public void setApplicationPassord(String applicationPassord) {
        this.applicationPassord = applicationPassord;
    }

    public String getApplicationID() {
        return applicationID;
    }
    public String getApplicationPassord() {
        return applicationPassord;
    }



}
