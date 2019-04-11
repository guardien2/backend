import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatTabsModule} from '@angular/material/tabs';
import {MatListModule} from '@angular/material/list';
import {HttpClientModule} from '@angular/common/http';
import {NamesComponent } from './names/names.component';
import { FqnNameComponent } from './fqn-name/fqn-name.component';
import { SearchComponent } from './search/search.component';
import {TextFieldModule} from '@angular/cdk/text-field';
import {MatButtonModule} from '@angular/material/button';


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
    MatButtonModule

  ],
  providers: [FqnNameComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
