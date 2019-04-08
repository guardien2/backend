import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'FrontEnd';
  constructor(private http: HttpClient) {}
    // tslint:disable-next-line:no-inferrable-types
    name: string ;
    fqn: string ;
    onNameKeyUp(event: any) {
        this.name = event.target.value;
    }

    getName() {
        this.http.get('http://localhost:9080/BackEnd/app/admin/class')
        .subscribe(
            (data: any[]) => {
                this.name = data[1].name;
                this.fqn = data[1].fqn;
                console.log(data);
            }
        );
    }
    test() {
        return 'hej';
    }

}
