import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MotorConfigComponent } from './motor-config.component';

describe('MotorConfigComponent', () => {
  let component: MotorConfigComponent;
  let fixture: ComponentFixture<MotorConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MotorConfigComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MotorConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
