package org.PolandSignsDetection;

import java.io.File;

public interface constants {

    // Nazwa folderu z trasami.
    public String tracesDirName = "Traces" + File.separator;

    // Nazwa klucza numeru trasy.
    public String traceNumberKey= "Numer_Trasy";

    // Nazwa klucza znaków na trasie.
    public String traceSignOrderKey = "Trasa";

    // Długość geograficzna w stringu.
    public String Longitude = "Dł. geograficzna";

    // Szerokość geograficzna w stringu.
    public String Latitude = "Szer. geograficzna";

    // Czas przetworzenia jednej klatki.
    public String InterferenceTime= "Czas przetworzenia obrazu";

    // Minimalna wartość detekcji
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.85f;

    // Minimalna wartość predkości pozwalającej na detekcję kolejnego znaku.
    public int MinimalSpeedToEnableDetecting = 5;

}
