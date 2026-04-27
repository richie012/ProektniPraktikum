import { createContext, useContext, useEffect, useState } from "react";
import API, { getAuthToken, setAuthToken } from "../api/api";

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [isAuthLoading, setIsAuthLoading] = useState(true);

    const decodeTokenUser = (token) => {
        try {
            const payloadPart = token.split(".")[1];
            if (!payloadPart) {
                return null;
            }
            const normalized = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
            const payload = JSON.parse(atob(normalized));
            if (!payload?.sub) {
                return null;
            }
            return { email: payload.sub };
        } catch {
            return null;
        }
    };

    const loadCurrentUser = async () => {
        try {
            const me = await API.get("/auth/me");
            setUser(me.data);
            return me.data;
        } catch {
            return null;
        }
    };

    useEffect(() => {
        const initAuth = async () => {
            const token = getAuthToken();
            if (!token) {
                setIsAuthLoading(false);
                return;
            }

            const tokenUser = decodeTokenUser(token);
            if (tokenUser) {
                setUser(tokenUser);
            }

            await loadCurrentUser();
            setIsAuthLoading(false);
        };

        initAuth();
    }, []);

    const login = async (email, password) => {
        const response = await API.post("/auth/login", { email, password });
        const token = response.data?.token;
        if (!token) {
            throw new Error("TOKEN_MISSING");
        }
        setAuthToken(token);
        const tokenUser = decodeTokenUser(token);
        setUser(tokenUser || { email });
        await loadCurrentUser();
    };

    const register = async (email, password, role) => {
        const response = await API.post("/auth/register", { email, password, role });
        const token = response.data?.token;
        if (!token) {
            throw new Error("TOKEN_MISSING");
        }
        setAuthToken(token);
        const tokenUser = decodeTokenUser(token);
        setUser(tokenUser || { email, role });
        await loadCurrentUser();
    };

    const logout = () => {
        setAuthToken(null);
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, isAuthLoading, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}