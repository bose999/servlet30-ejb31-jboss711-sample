package jp.techie.sample.exception;

/**
 * サービス実行時RuntimeException
 * 
 * @author bose999
 *
 */
@SuppressWarnings("serial")
public class SampleException extends RuntimeException {
    
    /**
     * コンストラクタ
     * 
     * @param reason Exception reason
     */
    public SampleException(String reason){
        super(reason);
    }
}
