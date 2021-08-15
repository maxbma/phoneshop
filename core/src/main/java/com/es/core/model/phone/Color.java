package com.es.core.model.phone;

public class Color {
    private Long id;
    private String code;

    public Color(){}

    public Color(Long id, String code){
        this.id = id;
        this.code = code;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || obj.getClass()!=this.getClass()) return false;
        Color color = (Color)obj;
        return this.id.longValue() == color.id.longValue() &&
                this.code.equals(color.code);
    }

    @Override
    public int hashCode(){
        return id.intValue() + code.hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }
}
