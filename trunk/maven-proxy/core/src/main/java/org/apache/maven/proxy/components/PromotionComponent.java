package org.apache.maven.proxy.components;


/**
 * Controls the promotion of artifacts from remote repositories to local repositories 
 * @author Ben Walding
 */
public interface PromotionComponent
{
    void addPromotionRequest( Promotion promotion );
}