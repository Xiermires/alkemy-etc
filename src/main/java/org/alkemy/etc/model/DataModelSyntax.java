package org.alkemy.etc.model;

import org.alkemy.exception.AlkemyException;

public class DataModelSyntax extends AlkemyException
{
    private static final long serialVersionUID = 1L;
    
    public DataModelSyntax(String message, Object... args)
    {
        super(message, args);
    }    
}
