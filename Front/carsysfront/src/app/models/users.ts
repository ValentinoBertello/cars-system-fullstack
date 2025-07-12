export interface LoginRequestDto {
  email: string;
  password: string;
}

export interface LoginResponseDto {
  token: string;
  username: string;
  message: string;
}

export interface UserPost {
    name: string;
    lastname: string;
    email: string;
    password: string;
    roleNames: string[];
}

export interface UserGet {
    id: number;                  
    name: string;            
    lastname: string;        
    email: string;              
    active: boolean;             
    roles: string[];             
}