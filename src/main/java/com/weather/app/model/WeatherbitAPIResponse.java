package com.weather.app.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class WeatherbitAPIResponse {
    private int count;
    private List<Data> data;

	public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        public double getAppTemp() {
			return appTemp;
		}
		public void setAppTemp(double appTemp) {
			this.appTemp = appTemp;
		}
		public int getAqi() {
			return aqi;
		}
		public void setAqi(int aqi) {
			this.aqi = aqi;
		}
		public String getCityName() {
			return cityName;
		}
		public void setCityName(String cityName) {
			this.cityName = cityName;
		}
		public int getClouds() {
			return clouds;
		}
		public void setClouds(int clouds) {
			this.clouds = clouds;
		}
		public String getCountryCode() {
			return countryCode;
		}
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}
		public String getDatetime() {
			return datetime;
		}
		public void setDatetime(String datetime) {
			this.datetime = datetime;
		}
		public double getDewpt() {
			return dewpt;
		}
		public void setDewpt(double dewpt) {
			this.dewpt = dewpt;
		}
		public double getDhi() {
			return dhi;
		}
		public void setDhi(double dhi) {
			this.dhi = dhi;
		}
		public double getDni() {
			return dni;
		}
		public void setDni(double dni) {
			this.dni = dni;
		}
		public double getElevAngle() {
			return elevAngle;
		}
		public void setElevAngle(double elevAngle) {
			this.elevAngle = elevAngle;
		}
		public double getGhi() {
			return ghi;
		}
		public void setGhi(double ghi) {
			this.ghi = ghi;
		}
		public double getGust() {
			return gust;
		}
		public void setGust(double gust) {
			this.gust = gust;
		}
		public double gethAngle() {
			return hAngle;
		}
		public void sethAngle(double hAngle) {
			this.hAngle = hAngle;
		}
		public double getLat() {
			return lat;
		}
		public void setLat(double lat) {
			this.lat = lat;
		}
		public double getLon() {
			return lon;
		}
		public void setLon(double lon) {
			this.lon = lon;
		}
		public String getObTime() {
			return obTime;
		}
		public void setObTime(String obTime) {
			this.obTime = obTime;
		}
		public String getPod() {
			return pod;
		}
		public void setPod(String pod) {
			this.pod = pod;
		}
		public double getPrecip() {
			return precip;
		}
		public void setPrecip(double precip) {
			this.precip = precip;
		}
		public double getPres() {
			return pres;
		}
		public void setPres(double pres) {
			this.pres = pres;
		}
		public int getRh() {
			return rh;
		}
		public void setRh(int rh) {
			this.rh = rh;
		}
		public int getSlp() {
			return slp;
		}
		public void setSlp(int slp) {
			this.slp = slp;
		}
		public double getSnow() {
			return snow;
		}
		public void setSnow(double snow) {
			this.snow = snow;
		}
		public double getSolarRad() {
			return solarRad;
		}
		public void setSolarRad(double solarRad) {
			this.solarRad = solarRad;
		}
		public List<String> getSources() {
			return sources;
		}
		public void setSources(List<String> sources) {
			this.sources = sources;
		}
		public String getStateCode() {
			return stateCode;
		}
		public void setStateCode(String stateCode) {
			this.stateCode = stateCode;
		}
		public String getStation() {
			return station;
		}
		public void setStation(String station) {
			this.station = station;
		}
		public String getSunrise() {
			return sunrise;
		}
		public void setSunrise(String sunrise) {
			this.sunrise = sunrise;
		}
		public String getSunset() {
			return sunset;
		}
		public void setSunset(String sunset) {
			this.sunset = sunset;
		}
		public double getTemp() {
			return temp;
		}
		public void setTemp(double temp) {
			this.temp = temp;
		}
		public String getTimezone() {
			return timezone;
		}
		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}
		public long getTs() {
			return ts;
		}
		public void setTs(long ts) {
			this.ts = ts;
		}
		public double getUv() {
			return uv;
		}
		public void setUv(double uv) {
			this.uv = uv;
		}
		public int getVis() {
			return vis;
		}
		public void setVis(int vis) {
			this.vis = vis;
		}
		public Weather getWeather() {
			return weather;
		}
		public void setWeather(Weather weather) {
			this.weather = weather;
		}
		public String getWindCdir() {
			return windCdir;
		}
		public void setWindCdir(String windCdir) {
			this.windCdir = windCdir;
		}
		public String getWindCdirFull() {
			return windCdirFull;
		}
		public void setWindCdirFull(String windCdirFull) {
			this.windCdirFull = windCdirFull;
		}
		public int getWindDir() {
			return windDir;
		}
		public void setWindDir(int windDir) {
			this.windDir = windDir;
		}
		public double getWindSpd() {
			return windSpd;
		}
		public void setWindSpd(double windSpd) {
			this.windSpd = windSpd;
		}
    
		@JsonProperty("app_temp")
        private double appTemp;
        private int aqi;
        @JsonProperty("city_name")
        private String cityName;
        private int clouds;
        @JsonProperty("country_code")
        private String countryCode;
        private String datetime;
        private double dewpt;
        private double dhi;
        private double dni;
        @JsonProperty("elev_angle")
        private double elevAngle;
        private double ghi;
        private double gust;
        @JsonProperty("h_angle")
        private double hAngle;
        private double lat;
        private double lon;
        @JsonProperty("ob_time")
        private String obTime;
        private String pod;
        private double precip;
        private double pres;
        private int rh;
        private int slp;
        private double snow;
        @JsonProperty("solar_rad")
        private double solarRad;
        private List<String> sources;
        @JsonProperty("state_code")
        private String stateCode;
        private String station;
        private String sunrise;
        private String sunset;
        private double temp;
        private String timezone;
        private long ts;
        private double uv;
        private int vis;
        private Weather weather;
        @JsonProperty("wind_cdir")
        private String windCdir;
        @JsonProperty("wind_cdir_full")
        private String windCdirFull;
        @JsonProperty("wind_dir")
        private int windDir;
        @JsonProperty("wind_spd")
        private double windSpd;
    }
    
    public static class Weather {
        private String description;
        private int code;
        private String icon;

        // Getters and Setters
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
