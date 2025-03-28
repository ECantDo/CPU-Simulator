package Assembler.Exceptions;

public class OpcodeExistsException extends RuntimeException{
	public OpcodeExistsException(String errorMessage, Throwable error){
		super(errorMessage, error);
	}

	public OpcodeExistsException(){
		super("An opcode using that name already exists, use a different name.");
	}
}
