package org.apache.maven.proxy.components.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.proxy.components.Promotion;
import org.apache.maven.proxy.components.PromotionComponent;

/**
 * @author Ben Walding
 */
public class DefaultPromotionComponent implements PromotionComponent
{
    private List requests = new ArrayList();

    public void addPromotionRequest( Promotion request )
    {
        requests.add( request );
    }
}
