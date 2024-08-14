package com.oxylabscurl.shell;

import lombok.Data;

import java.io.Serializable;

@Data
public class IpInfo implements Serializable {
	private String ip;
	private String city;
	private String hostname;
	private String region;
	private String country;
	private String loc;
	private String org;
	private String postal;
	private String timezone;
	private String readme;
}

