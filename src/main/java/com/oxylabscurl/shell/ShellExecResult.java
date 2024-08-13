package com.oxylabscurl.shell;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Hui.Wang [huzi.wh@gmail.com]
 * @version : 1.0
 * @date : 2024/8/13
 */
@Data
public class ShellExecResult implements Serializable {
    private String stdout;
    private String error;
    private int exitCode;
}
