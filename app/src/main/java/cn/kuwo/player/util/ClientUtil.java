package cn.kuwo.player.util;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Connection;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lovely on 2018/6/26
 */
public class ClientUtil {
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
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
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient = okHttpClient.newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Request.Builder builder = request.newBuilder();
                            List<String> headerValues = request.headers("url_name");

                            if (headerValues != null && headerValues.size() > 0) {
                                String headerValue = headerValues.get(0);
                                HttpUrl newBaseUrl = null;
                                if ("host".equals(headerValue)) {
                                    newBaseUrl=HttpUrl.parse(CONST.APIURL.HOST + CONST.APIURL.ROUTER);
                                }else if("pre".equals(headerValue)){
                                    newBaseUrl=HttpUrl.parse(CONST.APIURL.PREDOMAIN + CONST.APIURL.ROUTER);
                                }
                                HttpUrl oldHttpUrl = request.url();
                                HttpUrl newFullUrl = oldHttpUrl
                                        .newBuilder()
                                        .scheme(newBaseUrl.scheme())
                                        .host(newBaseUrl.host())
                                        .port(newBaseUrl.port())
                                        .build();
                                builder
                                        .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                        .addHeader("Aobeef-Session-Token", SharedHelper.read("sessionToken"))
                                        .addHeader("Accept", "*/*")
                                        .url(newBaseUrl)
                                        .build();
                                return chain.proceed(builder.url(newFullUrl).build());

                            } else {
                                request = chain.request()
                                        .newBuilder()
                                        .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                        .addHeader("Aobeef-Session-Token", SharedHelper.read("sessionToken"))
                                        .addHeader("Accept", "*/*")
                                        .build();
                                return chain.proceed(request);
                            }

                        }
                    })
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
