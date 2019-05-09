import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTabsModule } from '@angular/material/tabs';
import { MatListModule } from '@angular/material/list';
import { HttpClientModule } from '@angular/common/http';

import { FqnNameComponent } from './fqn-name/fqn-name.component';
import { SearchComponent } from './search/search.component';
import { TextFieldModule } from '@angular/cdk/text-field';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatTreeModule } from '@angular/material/tree';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import {MatGridListModule} from '@angular/material/grid-list';
import { TreeGridModule } from '@syncfusion/ej2-angular-treegrid';



@NgModule({
    declarations: [
        AppComponent,
        FqnNameComponent,
        SearchComponent


    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        MatTabsModule,
        HttpClientModule,
        MatListModule,
        TextFieldModule,
        MatButtonModule,
        MatTableModule,
        MatTreeModule,
        MatIconModule,
        MatSelectModule,
        MatInputModule,
        MatGridListModule,
        TreeGridModule


    ],
    providers: [FqnNameComponent],
    bootstrap: [AppComponent]
})
export class AppModule { }
