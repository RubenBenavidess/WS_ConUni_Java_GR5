namespace ec.edu.monster.Modelos;

public static class ConstantesRed
{
    public const string IP_WEB_SERVICES = "192.168.18.9";
    //public const string IP_WEB_SERVICES = "localhost";

    public const string UrlJavaSoap = $"http://{IP_WEB_SERVICES}:8080/04.SERVIDOR/WSConversorUnidades";
    public const string UrlDotNetSoap = $"http://{IP_WEB_SERVICES}:9098/WSConversorUnidades.svc";
    public const string UrlDotNetRest = $"http://{IP_WEB_SERVICES}:9099/api";
    public const string UrlJavaRest = $"http://{IP_WEB_SERVICES}:8080/04.SERVIDOR/api";
}
