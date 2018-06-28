package cn.kuwo.player.util;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lovely on 2018/6/26
 */
public class ClientUtil {
    public static OkHttpClient getUnsafeOkHttpClient(){
        try{
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient = okHttpClient.newBuilder()
                     .addInterceptor(new Interceptor() {
                         @Override
                         public Response intercept(Chain chain) throws IOException {
                             Request request=chain.request()
                                     .newBuilder()
                                     .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                     .addHeader("Aobeef-Session-Token",SharedHelper.read("sessionToken"))
                                     .addHeader("Accept", "*/*")
                                     .build();
                             return chain.proceed(request);
                         }
                     })
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();
            return okHttpClient;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
