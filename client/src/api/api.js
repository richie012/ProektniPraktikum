import axios from "axios";

export const AUTH_TOKEN_KEY = "authToken";

const API = axios.create({
    baseURL: "http://localhost:8080/api",
});

function applyTokenToDefaults(token) {
    if (token) {
        API.defaults.headers.common.Authorization = `Bearer ${token}`;
    } else {
        delete API.defaults.headers.common.Authorization;
    }
}

export function setAuthToken(token) {
    if (token) {
        localStorage.setItem(AUTH_TOKEN_KEY, token);
        applyTokenToDefaults(token);
        return;
    }
    localStorage.removeItem(AUTH_TOKEN_KEY);
    applyTokenToDefaults(null);
}

export function getAuthToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
}

API.interceptors.request.use((config) => {
    const token = getAuthToken();
    if (token) {
        if (!config.headers) {
            config.headers = {};
        }
        if (typeof config.headers.set === "function") {
            config.headers.set("Authorization", `Bearer ${token}`);
        } else {
            config.headers.Authorization = `Bearer ${token}`;
        }
    }
    return config;
});

applyTokenToDefaults(getAuthToken());

export default API;