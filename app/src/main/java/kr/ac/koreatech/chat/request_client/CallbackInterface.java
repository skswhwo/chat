package kr.ac.koreatech.chat.request_client;


interface CallbackInterface {
    // 생성시의 Callback
    void success(Object object);

    // 실패시의 Callback
    void error(Throwable throwable);
}