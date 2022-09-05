package org.PolandSignsDetection;

import java.io.File;

interface constants {

    // Nazwa folderu z trasami.
    public String tracesDirName = "Traces" + File.separator;

    // Nazwa klucza numeru trasy.
    public String traceNumberKey= "Numer_Trasy";

    // Nazwa klucza znaków na trasie.
    public String traceSignOrderKey = "Trasa";

    // Długość geograficzna w stringu.
    public String Longitude = "Longitude";

    // Szerokość geograficzna w stringu.
    public String Latitude = "Latitude";

    // Czas przetworzenia jednej klatki.
    public String InterferenceTime= "Interference Time";
}
