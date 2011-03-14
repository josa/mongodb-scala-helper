package br.com.gfuture.mongodbhelper.annotations;

/**
 * Representa como será tratado a persistencia em cascata
 * por hora terá somente CascadeType.SAVE
 */
public enum CascadeType {
    NONE, SAVE
}