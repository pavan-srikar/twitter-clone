import { AbstractControl, AsyncValidatorFn, ValidationErrors } from "@angular/forms";
import { Observable } from "rxjs";
import {  map } from "rxjs/operators";
import { AuthService } from "../services/auth.service";


export function usernameValidator(authService:AuthService): AsyncValidatorFn{
    return (control:AbstractControl):Observable<ValidationErrors|null>=>{
        return authService.findAllUsernames().pipe(
            map(usernames => {
                const username = usernames.find(username => username.toLowerCase() == control.value.toLowerCase());
                return username ? {usernameExists:true} : null;
            })
        )
    }
}