package cz.inqool.eas.eil.authority;

/**
 * Represents entity that is saved in Narodni databaze autorit.
 * This database stores persons, places etc. and all their name variants. These variants can be obtained via request on
 * specified API and saved into EIL.
 */
public interface Authority {

    String getAuthorityCode();

//    List<String> getNameVariants();

//    String getVersion();
}
