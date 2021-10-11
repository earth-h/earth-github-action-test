package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {
    // 이렇게 override해야 각 상황에 맞게 메시지 받아서 exception trace 넣을 수 있습니다.

    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }
}
